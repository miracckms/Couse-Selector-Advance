package com.yeditepe.courseselector.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleMetrics {
    private int totalGaps;           // Günlük boşlukların toplamı (dakika)
    private int daysWithClasses;     // Dersi olan gün sayısı
    private String earliestStart;    // En erken ders başlangıcı
    private String latestEnd;        // En geç ders bitişi
    private int score;               // Genel puan (düşük = daha iyi)
}
