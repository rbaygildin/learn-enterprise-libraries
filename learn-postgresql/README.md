# Recursive queries

1. Select subset of original set of digits which's sum is not more than specified amount (greedy algorithm)
```sql
WITH RECURSIVE r(row,
                 res,
                 records) AS (SELECT 1, '{}' :: INTEGER [], (SELECT array_agg(s) FROM generate_series(1, 20) s) s
    UNION SELECT row + 1, array_append(res, records [1]), records [2:]
          FROM r
          WHERE array_length(records, 1) > 0
            AND coalesce((SELECT sum(v) FROM unnest(res) v), 0) < 10)
SELECT *
FROM r ORDER BY row DESC LIMIT 1;
```

2. Get set of elements the number of each of which in the total amount is not more than the batch limit, and as many as possible of such unique values are selected (greedy alogorithm)
```sql
WITH RECURSIVE dates_filter(r,
                 chosen,
                 overall_n,
                 remaining) AS (SELECT 1,
                                       '{}' :: TEXT [],
                                       0,
                                       (SELECT array_agg(kv)
                                        FROM (SELECT json_build_object('date', d, 'n_rows', n_rows) kv
                                              FROM (SELECT d, count(1) n_rows
                                                    FROM (VALUES ('jan'),
                                                                 ('feb'),
                                                                 ('feb'),
                                                                 ('dec'),
                                                                 ('jul'),
                                                                 ('jul')) dates (d)
                                                    GROUP BY d) dates
                                              ORDER BY n_rows ASC) freqs) freqs
    UNION ALL SELECT r + 1,
                     array_append(chosen, remaining [ 1 ]->>'date'),
                     overall_n + (remaining [ 1 ]->>'n_rows') :: INTEGER,
                     remaining [ 2 : ]
              FROM dates_filter
              WHERE array_length(remaining, 1) >= 0
                AND overall_n + coalesce((remaining [ 1 ]->>'n_rows') :: INTEGER, 0) <= :batchLimit)
SELECT chosen
FROM dates_filter
ORDER BY r DESC
LIMIT 1;
```

# System catalog operations

1. Create SQL query of deletion data of all tables specified in 'public' scheme
``` sql
SELECT string_agg('DELETE FROM ' || tablename || ';', E'\n')  
FROM pg_tables  
WHERE schemaname = 'public';
```
