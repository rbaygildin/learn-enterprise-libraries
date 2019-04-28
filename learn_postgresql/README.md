# Recursive queries

1. Select subset of original set of digits which's sum is not more than specified amount
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

# System catalog operations

1. Create SQL query of deletion data of all tables specified in 'public' scheme
``` sql
SELECT string_agg('DELETE FROM ' || tablename || ';', E'\n')  
FROM pg_tables  
WHERE schemaname = 'public';
```
