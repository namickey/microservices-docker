ps aux | grep 'flask run' | grep -v grep | awk '{ print "kill -9", $2 }' | sh
