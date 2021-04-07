package smolka.smsapi.service.parameters_service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.model.InternalParameter;
import smolka.smsapi.repository.ParametersRepository;
import smolka.smsapi.service.parameters_service.ParametersService;

@Service
public class ParametersServiceImpl implements ParametersService {

    private static final String SMS_HUB_API_KEY_PARAM_NAME = "SMS_HUB_API_KEY";
    private static final String PERCENTAGE_FOR_MARKUPPER_PARAM_NAME = "PERCENTAGE_MARK_UPPER";
    private static final String QIWI_SECRET_KEY_PARAM_NAME = "SECRET_KEY_QIWI";

    @Autowired
    private ParametersRepository parametersRepository;

    @Override
    public void saveSmsHubApiKey(String apiKey) {
        update(SMS_HUB_API_KEY_PARAM_NAME, apiKey);
    }

    @Override
    @Transactional
    public void savePercentageForMarkUpper(Integer percentage) {
        update(PERCENTAGE_FOR_MARKUPPER_PARAM_NAME, percentage);
    }

    @Override
    @Transactional
    public void saveQiwiSecretKey(String qiwiSecretKey) {
        update(QIWI_SECRET_KEY_PARAM_NAME, qiwiSecretKey);
    }

    @Override
    public String getSmsHubApiKey() {
        return getStringVal(SMS_HUB_API_KEY_PARAM_NAME);
    }

    @Override
    public Integer getPercentageForMarkUpper() {
        return Integer.parseInt(getStringVal(PERCENTAGE_FOR_MARKUPPER_PARAM_NAME));
    }

    @Override
    public String getQiwiSecretKey() {
        return getStringVal(QIWI_SECRET_KEY_PARAM_NAME);
    }

    private void update(String key, Object value) {
        InternalParameter parameter = parametersRepository.findByName(key);
        if (parameter == null) {
            throw new NullPointerException("Такого параметра не существует " + key);
        }
        parameter.setValue(value.toString());
        parametersRepository.save(parameter);
    }

    private String getStringVal(String key) {
        InternalParameter parameter = parametersRepository.findByName(key);
        if (parameter == null || parameter.getValue() == null) {
            throw new NullPointerException("Такого параметра не существует " + key);
        }
        return parameter.getValue();
    }
}
