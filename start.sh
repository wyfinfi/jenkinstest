# 拉取代码
git pull

# 安装依赖和打包
#npm install --registry=https://registry.npm.taobao.org/ && npm run build

# 删除容器
docker rm -f demo1 &> /dev/null

#启动容器
docker run -d -p 8080:80 -v $PWD/src:/usr/share/nginx/html nginx
