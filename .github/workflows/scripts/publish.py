import sys
import requests

data = {}

data['content'] = ' '
print(data, flush=True)

result = requests.post(sys.argv[3],
                        data=data, 
                        files={'upload_file': (sys.argv[2] + '-' + sys.argv[4] + '.jar', 
                        open(sys.argv[1], "rb").read())})
print(result, flush=True)
print(result.content, flush=True)
