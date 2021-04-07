package smolka.smsapi.service.qiwi_service;

import smolka.smsapi.dto.qiwi.input.QiwiAddBalanceRequest;
import smolka.smsapi.dto.qiwi.QiwiCreateBillResponse;
import smolka.smsapi.exception.PaymentSystemException;
import smolka.smsapi.exception.UserNotFoundException;

public interface BillService {
    QiwiCreateBillResponse createQiwiBillAndReturnLink(QiwiAddBalanceRequest qiwiAddBalanceRequest) throws UserNotFoundException, PaymentSystemException;
    void succeedBill(String billId) throws PaymentSystemException;
    void closeAllStaleBills();
}
