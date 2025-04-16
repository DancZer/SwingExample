This is a POC project to make a cache without modifying the business logic which relays on a lazy load. 
Ideal solution would be using targeted Queries with fetch and join. If that's not possible for a read only purpose this could be a valuable solution.


Start database
```shell
docker run -d -e POSTGRES_DB=postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 --name swing-postgres postgres
```

Example results:
Execution time: 1,763 seconds (1762,77 ms) with cache:false
Execution time: 1,385 seconds (1384,57 ms) with cache:false
Execution time: 1,255 seconds (1255,11 ms) with cache:false
Execution time: 1,314 seconds (1313,76 ms) with cache:false
Execution time: 0,459 seconds (458,80 ms) with cache:true
Execution time: 0,430 seconds (429,58 ms) with cache:true
Execution time: 0,401 seconds (401,29 ms) with cache:true
