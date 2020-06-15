import sys
from datetime import datetime

import requests

# Parameter syntax
# 0: script name
# 1: Target file
# 2: Published starting file name
# 3: Discord-Webhook-Address
# 4: Commit-SHA

data = {}

data['content'] = ' '  # <- What will be written in the chat before the file
print(data, flush=True)

result = requests.post(sys.argv[3], data=data, files={'upload_file': (sys.argv[2] + "_" + str(datetime.now().strftime('%Y-%m-%d_%H-%M')) + '_commit_' + sys.argv[4][:6] + '.jar', open(sys.argv[1], "rb").read())})
print(result, flush=True)
print(result.content, flush=True)