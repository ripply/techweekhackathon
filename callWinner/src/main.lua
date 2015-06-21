-- This comment enforces unit-test coverage for this file:
-- coverage: 0

channel.answer()

message = "Hello and Congratulations! You have won the selfie competition. Stop by the visitor office before leaving the park to pick up your free season pass. See you soon!"

sms = require "summit.sms"
 
to = channel.data.dnis
from = channel.data.ani
 
ok, err = sms.send(to, from, message)

channel.sleep(3)
channel.say(message)

channel.hangup()

