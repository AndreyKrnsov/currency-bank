package com.example.test.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDto {

    private String date;
    private String time;
    private String year;
    private String type;
    private String accounts;
    private String amount;
    private String rate;
}