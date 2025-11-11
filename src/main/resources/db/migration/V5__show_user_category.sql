-- ✅ 유저별 가장 많이 활동한 카테고리 1개 조회
SELECT
    category_group_name AS top_category,
    COUNT(*) AS activity_count
FROM user_activities
WHERE user_id = :userId                -- 특정 유저만
  AND action_type = 'QUEST_COMPLETE'   -- 퀘스트 완료만 카운트
GROUP BY category_group_name
ORDER BY activity_count DESC
LIMIT 1;
