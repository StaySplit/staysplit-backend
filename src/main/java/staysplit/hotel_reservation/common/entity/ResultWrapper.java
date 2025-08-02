package staysplit.hotel_reservation.common.entity;

import lombok.Getter;

@Getter
public class ResultWrapper<T> {
    private String code;
    private T data;

    public ResultWrapper(String code, T data) {
        this.code = code;
        this.data = data;
    }

}