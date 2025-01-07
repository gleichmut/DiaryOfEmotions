package com.fuxkinghatred.diaryofemotions.callbacks;

/**
 * Интерфейс обратного вызова для проверки валидности IP-адреса.
 */
public interface IpAddressValidationCallback {

    /**
     * Вызывается, когда проверка валидности IP-адреса завершена.
     *
     * @param isValid true, если IP-адрес действителен, иначе false
     */
    void onIpAddressValidationReady(boolean isValid);
}