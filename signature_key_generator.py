#!/usr/bin/env python3
import string, random

print(''.join(random.SystemRandom().choice(string.ascii_uppercase + string.ascii_lowercase + string.punctuation + string.digits) for _ in range(32)))