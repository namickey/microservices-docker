cd `dirname $0`
export FLASK_APP=dashboard.py
export FLASK_ENV=development
flask run --host 0.0.0.0 --port 8089 &
