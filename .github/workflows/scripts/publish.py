import sys
import requests
import os

data = {}

data['content'] = ' '
print(data, flush=True)

result = requests.post(os.environ['secrets.DiscordWebHook'],
                        data=data, 
                        files={'upload_file': (sys.argv[2] + '-' + os.environ['GITHUB_EVENT_NAME'] + '-' + os.environ['GITHUB_HEAD_NAME'] + '.jar', 
                        open(sys.argv[1], "rb").read())})
print(result, flush=True)
print(result.content, flush=True)