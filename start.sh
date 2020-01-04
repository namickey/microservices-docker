cd $HOME
source .profile
export JAVA_HOME=$HOME/.sdkman/candidates/java/8.0.232-open
export PATH=$PATH:$JAVA_HOME/bin

cd `dirname $0`
./redisstart.sh
./mosquittostart.sh
./card/authorize/start.sh
./cafis/start.sh
./psp/start.sh
./merchant/start.sh
./dashboard/start.sh
