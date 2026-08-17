#!/bin/bash
#  -keypass 'Jkdi23@%kd@lskCs' ----- 使用这个参数导致密码与keystore参数密码不一致会报错。
keytool -genkeypair -keyalg Ed25519 -alias jwt-ed25519-key -keystore jwt-ed25519-keystore.p12 -storetype PKCS12 -storepass 'ppJD3%$jkd@#sk)sk20-Lix' -validity 3650 -dname "CN=AuthService,OU=SecurityTeam,O=LiJiu,C=CN"
