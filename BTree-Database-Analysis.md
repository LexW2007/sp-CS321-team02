# BTree Database Analysis
## Team 02

The table below shows the results of analyzing selected B-Trees and their top results from the
corresponding database tables.

| Tree Type             | degree | frequency | Question                                                                            |
|-----------------------|--------|-----------|-------------------------------------------------------------------------------------|
| `accepted-ip`         | 100    | 10        | What is the most common first three IP digits in the top 10 entries?                |
| `invalid-time`        | 100    | 25        | What is the range (highest-lowest) of the top 25 entries?                           |
| `failed-ip`           | 100    | 50        | What is the first three IP digits of the top 2 entries of 50 total entries?         |
| `reverseaddress-ip`   | 100    | 25        | Is the top entry `reverse` or `Address` type in the top 25 entries?                 |
| `reverseaddress-time` | 100    | 25        | Which hour was the most prevalent for a reverse break in attempt in the top entries? |
| `user-ip`             | 100    | 25        | What is the predominant user in the top 25 entries?                                 |

## `accepted-ip`

### What is the most common first three IP digits in the top 10 entries?

The most common first three IP digits are `137`, appearing 3 times in the top 10 entries.

Database Output:
```bash
Accepted-111.222.107.90 25
Accepted-119.137.63.195 14
Accepted-137.189.88.215 12
Accepted-61.187.54.9 12
Accepted-137.189.241.248 11
Accepted-137.189.90.232 10
Accepted-119.137.62.123 9
Accepted-218.17.80.182 7
Accepted-222.240.177.42 7
Accepted-113.116.236.34 6
```

## `invalid-time`

### What is the range (highest-lowest) of the top 25 entries?

The highest frequency is `55` and the lowest frequency is `42`, so the range is `13`.

Database Output:
```bash
Invalid-11:38 55
Invalid-11:39 55
Invalid-23:55 55
Invalid-11:37 53
Invalid-12:24 53
Invalid-04:05 50
Invalid-16:02 50
Invalid-04:04 49
Invalid-08:44 49
Invalid-04:29 48
Invalid-08:06 48
Invalid-23:52 48
Invalid-04:34 47
Invalid-11:33 47
Invalid-07:43 46
Invalid-11:41 46
Invalid-03:17 45
Invalid-11:32 45
Invalid-11:40 45
Invalid-12:23 45
Invalid-03:13 44
Invalid-03:16 44
Invalid-23:54 44
Invalid-04:30 43
Invalid-23:53 42
```

## `failed-ip`

### What is the first three IP digits of the top 2 entries of 50 total entries?

The first three IP digits for the top 2 entries are `183`.

Database Output:
```bash
Failed-183.63.110.206 17340
Failed-183.238.178.195 14519
Failed-59.63.188.30 14384
Failed-139.219.191.138 10852
Failed-183.62.140.253 10852
Failed-183.192.189.131 8755
Failed-183.129.154.138 8670
Failed-183.63.172.52 7506
Failed-58.242.83.25 7192
Failed-14.116.171.251 4462
Failed-218.65.30.30 4189
Failed-183.129.170.130 3880
Failed-218.87.109.156 3571
Failed-182.100.67.120 3093
Failed-193.201.224.232 2992
Failed-183.238.25.86 2065
Failed-182.100.67.52 1712
Failed-183.57.42.19 1655
Failed-119.7.221.129 1648
Failed-119.7.221.137 1308
Failed-60.2.12.12 995
Failed-218.65.30.126 913
Failed-183.129.173.73 878
Failed-103.99.0.122 868
Failed-123.235.32.19 849
Failed-115.238.230.180 838
Failed-5.188.10.180 805
Failed-81.144.235.98 777
Failed-119.1.109.20 701
Failed-5.188.10.182 682
Failed-183.192.189.133 614
Failed-139.219.239.13 586
Failed-115.71.16.143 569
Failed-185.222.209.151 558
Failed-5.188.10.156 515
Failed-114.112.48.155 506
Failed-183.65.30.2 443
Failed-117.52.87.214 436
Failed-203.190.163.125 436
Failed-181.214.87.4 429
Failed-185.165.29.57 417
Failed-1.34.138.144 402
Failed-218.31.113.113 392
Failed-42.159.145.29 380
Failed-95.188.84.199 360
Failed-123.249.20.41 344
Failed-103.217.152.20 331
Failed-173.82.194.233 322
Failed-202.107.104.119 322
Failed-202.107.207.123 298
```

## `reverseaddress-ip`

### Is the top entry reverse or Address type in the top 25 entries?

The top entry is a `reverse` type.

Database Output:
```bash
reverse-183.192.189.131 8755
reverse-218.65.30.30 4192
reverse-218.65.30.126 912
reverse-183.192.189.133 613
reverse-95.188.84.199 371
Address-173.82.194.233 326
reverse-80.211.140.229 201
reverse-218.65.30.124 169
reverse-46.101.123.127 163
reverse-51.15.221.204 160
reverse-218.65.30.123 151
reverse-212.237.45.42 144
reverse-51.15.130.194 142
reverse-218.65.30.43 140
reverse-51.15.207.143 131
reverse-218.65.30.122 118
reverse-218.65.30.190 113
reverse-51.15.199.66 105
reverse-51.15.129.62 96
reverse-51.15.183.141 86
reverse-187.141.143.180 80
reverse-212.237.41.152 79
reverse-205.185.115.121 71
reverse-212.237.47.142 69
Address-122.224.73.38 67
```

## `reverseaddress-time`

### Which hour was the most prevalent for a reverse break in attempt in the top 25 entries?

The `11:00` hour is the most prevalent in the top 25 entries.

Database Output:
```bash
reverse-07:27 47
reverse-11:35 45
reverse-11:40 43
reverse-09:00 42
reverse-11:50 42
reverse-11:41 41
reverse-08:58 40
reverse-09:14 40
reverse-11:46 40
reverse-03:13 39
reverse-05:24 39
reverse-08:55 39
reverse-08:59 39
reverse-09:15 39
reverse-11:04 39
reverse-11:38 39
reverse-08:44 38
reverse-11:11 38
reverse-11:36 38
reverse-07:26 37
reverse-09:01 37
reverse-11:39 37
reverse-11:53 37
reverse-08:57 36
reverse-05:25 35
```

## `user-ip`

### Who is the predominant user in the top 25 entries?

The predominant user is `root`.

Database Output:
```bash
root-183.63.110.206 17340
root-183.238.178.195 14519
root-59.63.188.30 14384
root-139.219.191.138 10409
root-183.62.140.253 10409
root-183.192.189.131 8755
root-183.129.154.138 8670
root-183.63.172.52 7506
root-58.242.83.25 7192
root-14.116.171.251 4462
root-218.65.30.30 4189
root-183.129.170.130 3880
root-218.87.109.156 3571
admin-193.201.224.232 3496
root-182.100.67.120 3093
root-183.238.25.86 2065
root-182.100.67.52 1712
root-183.57.42.19 1655
root-119.7.221.137 1220
root-119.7.221.129 1060
root-60.2.12.12 978
root-218.65.30.126 913
root-183.129.173.73 878
root-123.235.32.19 849
root-81.144.235.98 777
```
