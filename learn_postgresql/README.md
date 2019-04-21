# System catalog operations

1. Create SQL query of deletion data of all tables specified in 'public' scheme
``` sql
SELECT string_agg('DELETE FROM ' || tablename || ';', E'\n')  
FROM pg_tables  
WHERE schemaname = 'public';
```
