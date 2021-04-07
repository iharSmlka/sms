package smolka.smsapi.service.qiwi_service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import smolka.smsapi.dto.qiwi.input.QiwiAddBalanceRequest;
import smolka.smsapi.dto.qiwi.QiwiCreateBillRequest;
import smolka.smsapi.dto.qiwi.QiwiCreateBillResponse;
import smolka.smsapi.exception.PaymentSystemException;
import smolka.smsapi.exception.UserNotFoundException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.Bill;
import smolka.smsapi.model.User;
import smolka.smsapi.repository.BillRepository;
import smolka.smsapi.repository.UserRepository;
import smolka.smsapi.service.parameters_service.ParametersService;
import smolka.smsapi.service.qiwi_service.BillService;
import smolka.smsapi.utils.DateTimeUtils;
import smolka.smsapi.utils.QiwiBillUtils;

import java.util.List;

@Service
public class BillServiceImpl implements BillService {

    private static final String QIWI_BILL_URL = "https://api.qiwi.com/partner/bill/v1/bills/%s";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ParametersService parametersService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private MainMapper mainMapper;
    @Value("${sms.api.bill_life_minutes}")
    private Integer billLifeMinutes;

    @Override
    @Transactional(rollbackFor = { UserNotFoundException.class, PaymentSystemException.class })
    public QiwiCreateBillResponse createQiwiBillAndReturnLink(QiwiAddBalanceRequest qiwiAddBalanceRequest) throws UserNotFoundException, PaymentSystemException {
        User user = userRepository.findUserByKey(qiwiAddBalanceRequest.getApiKey());
        if (user == null) {
            throw new UserNotFoundException("Данного юзера не существует!");
        }
        Bill bill = mainMapper.mapQiwiBillFromAddBalanceRequestAndUser(qiwiAddBalanceRequest, user, billLifeMinutes);
        bill = billRepository.save(bill);
        QiwiCreateBillRequest createBillRequest = mainMapper.mapQiwiBillRequestFromEntity(bill, billLifeMinutes);
        return getQiwiBillResponse(createBillRequest, bill.getId());
    }

    @Override
    @Transactional
    public void succeedBill(String billId) throws PaymentSystemException {
        Bill bill = billRepository.findBillById(billId);
        if (bill == null) {
            throw new PaymentSystemException("Такого счета не существует!");
        }
        bill.setIsPaid(true);
        billRepository.save(bill);
    }

    @Override
    @Transactional
    public void closeAllStaleBills() {
        List<Bill> unclosedBills = billRepository.findAllBillsByPlannedFinishDateLessThanEqual(DateTimeUtils.getUtcCurrentLocalDateTime());
        for (Bill b : unclosedBills) {
            b.setCloseDate(DateTimeUtils.getUtcCurrentLocalDateTime());
        }
        billRepository.saveAll(unclosedBills);
    }

    private QiwiCreateBillResponse getQiwiBillResponse(QiwiCreateBillRequest createBillRequest, String billId) throws PaymentSystemException {
        ResponseEntity<String> response = null;
        try {
            HttpEntity<QiwiCreateBillRequest> request = QiwiBillUtils.createBillRequest(createBillRequest, parametersService.getQiwiSecretKey());
            response = restTemplate.exchange(String.format(QIWI_BILL_URL, billId), HttpMethod.PUT, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new Exception("ошибка при общении с QIWI ");
            }
            return mainMapper.mapQiwiCreateBillResponseFromString(response.getBody());
        } catch (Exception exc) {
            throw new PaymentSystemException("ошибка при общении с QIWI ", response);
        }
    }
}
