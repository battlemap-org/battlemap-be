package org.battlemap.battlemapbe.dto.league;

/**
 * 동별 '내 포인트'와 '완료한 퀘스트 수'를 함께 내려주는 행 단위 DTO
 * - cityName : 부천시 등
 * - dongName : 역곡동 등
 * - myPoints : 해당 동에서 누적 포인트
 * - completedQuests : 해당 동에서 내가 완료한 퀘스트 개수
 */
public record DongPointRowDto(
        String cityName,
        String dongName,
        int myPoints,
        long completedQuests
) {}
