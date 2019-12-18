import sys
import requests
from datetime import datetime

data = {}

data['content'] = ' '
print(data, flush=True)

result = requests.post(sys.argv[3],
                        data=data, 
                        files={'upload_file': (sys.argv[2] + str(datetime.now().strftime('%Y-%m-%d_%H-%M')) + '-' + sys.argv[4][:4] + '.jar', 
                        open(sys.argv[1], "rb").read())})
print(result, flush=True)
print(result.content, flush=True)
