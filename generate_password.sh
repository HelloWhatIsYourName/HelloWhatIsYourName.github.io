#!/bin/bash

# 生成BCrypt密码哈希
echo "正在生成admin123的BCrypt哈希..."

# 使用Python生成BCrypt哈希
python3 -c "
import bcrypt
password = 'admin123'
hashed = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())
print('BCrypt哈希:', hashed.decode('utf-8'))
"