# Mongodb preparation

- installed by using brew 

```shell
$ brew tap mongodb/brew
$ brew install mongodb-community
```

- confirm installed

```shell
$ mongo --version                                                                            [/Users/kiyotatakeshi]
MongoDB shell version v4.2.3
git version: 6874650b362138df74be53d366bbefc321ea32d4
allocator: system
modules: none
build environment:
    distarch: x86_64
    target_arch: x86_64
```

- start 

```shell
$ mongod --dbpath /usr/local/var/mongodb/
```

- check active port for mongo

```shell
$ sudo lsof -i:27017                                                                         [/Users/kiyotatakeshi]
COMMAND   PID          USER   FD   TYPE             DEVICE SIZE/OFF NODE NAME
java     3651 kiyotatakeshi   82u  IPv6 0xfbcbf0ba9bebcf2f      0t0  TCP localhost:53927->localhost:27017 (ESTABLISHED)
java     3651 kiyotatakeshi  121u  IPv6 0xfbcbf0ba9bebbccf      0t0  TCP localhost:53928->localhost:27017 (ESTABLISHED)
mongod  16921 kiyotatakeshi    9u  IPv4 0xfbcbf0ba98fc0f27      0t0  TCP localhost:27017 (LISTEN)
mongod  16921 kiyotatakeshi   28u  IPv4 0xfbcbf0ba9a3711cf      0t0  TCP localhost:27017->localhost:53927 (ESTABLISHED)
mongod  16921 kiyotatakeshi   29u  IPv4 0xfbcbf0ba99672d67      0t0  TCP localhost:27017->localhost:53928 (ESTABLISHED)
```