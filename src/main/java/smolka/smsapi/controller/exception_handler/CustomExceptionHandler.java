package smolka.smsapi.controller.exception_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.enums.SmsConstants;
import smolka.smsapi.exception.*;

import javax.validation.ValidationException;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ReceiverException.class)
    public ResponseEntity<ServiceMessage<String>> handle(ReceiverException e) {
        String errorMsg = e.getReceiverErrorElement().getErrorMessage();
        logger.error("Ошибка ресивера c сообщением " + errorMsg + " и ответом " + e.getResponseFromReceiver(), e);
        ServiceMessage<String> serviceMessage = new ServiceMessage<>(SmsConstants.ERROR_STATUS.getValue(), SmsConstants.UNKNOWN_ERROR_MSG.getValue());
        return new ResponseEntity<>(serviceMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ActivationNotFoundException.class)
    public ResponseEntity<ServiceMessage<String>> handle(ActivationNotFoundException e) {
        logger.error("Такой активации не существует", e);
        ServiceMessage<String> serviceMessage = new ServiceMessage<>(SmsConstants.ERROR_STATUS.getValue(), SmsConstants.ACTIVATION_NOT_EXISTS_MSG.getValue());
        return new ResponseEntity<>(serviceMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoNumbersException.class)
    public ResponseEntity<ServiceMessage<String>> handle(NoNumbersException e) {
        logger.error("Нет доступных номеров", e);
        ServiceMessage<String> serviceMessage = new ServiceMessage<>(SmsConstants.ERROR_STATUS.getValue(), SmsConstants.NO_NUMBERS_MSG.getValue());
        return new ResponseEntity<>(serviceMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalOperationException.class)
    public ResponseEntity<ServiceMessage<String>> handle(IllegalOperationException e) {
        logger.error("Баланс юзера пуст", e);
        ServiceMessage<String> serviceMessage = new ServiceMessage<>(SmsConstants.ERROR_STATUS.getValue(), SmsConstants.ILLEGAL_OPERATION.getValue());
        return new ResponseEntity<>(serviceMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ServiceMessage<String>> handle(UserNotFoundException e) {
        logger.error("Юзер не найден", e);
        ServiceMessage<String> serviceMessage = new ServiceMessage<>(SmsConstants.ERROR_STATUS.getValue(), SmsConstants.USER_NOT_FOUND_MSG.getValue());
        return new ResponseEntity<>(serviceMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ServiceMessage<String>> handle(ValidationException e) {
        logger.error("Ошибка валидации", e);
        ServiceMessage<String> serviceMessage = new ServiceMessage<>(SmsConstants.ERROR_STATUS.getValue(), e.getMessage());
        return new ResponseEntity<>(serviceMessage, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(PaymentSystemException.class)
    public ResponseEntity<String> handle(PaymentSystemException e) {
        logger.error("Ошибка при оплате", e);
        return new ResponseEntity<>("Ошибка при оплате", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
