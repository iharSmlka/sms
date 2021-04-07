package smolka.smsapi.service.parameters_service;

public interface ParametersService {
    void saveSmsHubApiKey(String apiKey);

    void savePercentageForMarkUpper(Integer percentage);

    void saveQiwiSecretKey(String qiwiSecretKey);

    String getSmsHubApiKey();

    Integer getPercentageForMarkUpper();

    String getQiwiSecretKey();
}
