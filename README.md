# uap-clj-lambda

An Apache AWS Lambda wrapper around the [`uap-clj`](https://github.com/russellwhitaker/uap-clj) library providing Browser, O/S, and Device field extraction functions.

## Setup for development

### Running the test suite

The testrunner is [`speclj`](http://speclj.com).

```bash
→ lein clean && lein spec --reporter=d

Event handler output
- Looks up a useragent string and emits browser, device, and o/s information

Finished in 0.02981 seconds
1 examples, 0 failures
```

### Java version dependencies

This Lambda function runs in Amazon AWS Lambda's `java8` runtime, and has been shown to work
with these builds under both Java v1.7 and v1.8 SDKs (v1.8 strongly recommended):

```bash
→ java -version
java version "1.7.0_51"
Java(TM) SE Runtime Environment (build 1.7.0_51-b13)
Java HotSpot(TM) 64-Bit Server VM (build 24.51-b03, mixed mode)

→ java -version
java version "1.8.0_102"
Java(TM) SE Runtime Environment (build 1.8.0_102-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.102-b14, mixed mode)
```

## Operational deployment

### Deploy uberjar-packaged function to Amazon AWS Lambda

```bash
→ lein clean && lein uberjar
Compiling uap-clj-lambda.core
Created /Users/<username>/dev/uap-clj-lambda/target/uap-clj-lambda-0.2.0.jar
Created /Users/<username>/dev/uap-clj-lambda/target/simple-useragent-lambda.jar
```

`simple-useragent-lambda.jar` is the uberjar you'll upload to AWS when you create your Lambda function
using the `aws` CLI (select `--timeout` and `--memory-size` according to your needs):

```bash
→ aws lambda create-function \
    --region <aws_region> \
    --function-name my-useragent-lookup \
    --zip-file fileb://$(pwd)/target/simple-useragent-lambda.jar \
    --role arn:aws:iam::<amazon_id>:role/<lambda_exec_role> \
    --handler uap-clj-lambda.core.SimpleUseragentLookup \
    --runtime java8 \
    --timeout 60 \
    --memory-size 512 \
    --description "Look up a single useragent string and outputs browser, device, and o/s info"
```

A successful `create-function` will emit something like this:
```json
{
    "CodeSha256": "SOMESHA256ENCODEDSTRING",
    "FunctionName": "my-useragent-lookup",
    "CodeSize": 4369851,
    "MemorySize": 512,
    "FunctionArn": "arn:aws:lambda:<aws_region>:<amazon_id>:function:my-useragent-lookup",
    "Version": "$LATEST",
    "Role": "arn:aws:iam::<amazon_id>:role/<lambda_exec_role>",
    "Timeout": 60,
    "LastModified": "2015-10-28T21:11:49.589+0000",
    "Handler": "uap-clj-lambda.core.SimpleUseragentLookup",
    "Runtime": "java8",
    "Description": "Look up a single useragent string and outputs browser, device, and o/s info"
}
```

You can test with the CLI `invoke` method:

```bash
→ aws lambda invoke
    --invocation-type RequestResponse
    --function-name my-useragent-lookup --region <aws_region>
    --log-type Tail
    --payload '{"useragent":"Lenovo-A288t_TD/S100 Linux/2.6.35 Android/2.3.5 Release/02.29.2012 Browser/AppleWebkit533.1 Mobile Safari/533.1 FlyFlow/1.4"}'
    output.log
```

A successful `invoke` will emit something like this:
```json
{
    "LogResult": "U1RBUlQgUmVxdWVzdElkOiBjNmMwNjQ4My03ZGI4LTExZTUtODM3NC04OWQyMDc4MWMwMzIgVmVyc2lvbjogJExBVEVTVApFTkQgUmVxdWVzdElkOiBjNmMwNjQ4My03ZGI4LTExZTUtODM3NC04OWQyMDc4MWMwMzIKUkVQT1JUIFJlcXVlc3RJZDogYzZjMDY0ODMtN2RiOC0xMWU1LTgzNzQtODlkMjA3ODFjMDMyCUR1cmF0aW9uOiAxNzQuNzEgbXMJQmlsbGVkIER1cmF0aW9uOiAyMDAgbXMgCU1lbW9yeSBTaXplOiA1MTIgTUIJTWF4IE1lbW9yeSBVc2VkOiA2MSBNQgkK",
    "StatusCode": 200
}
```

You can have a look at the non-encoded version of the log output in the file you specified:

```json
→ cat output.log
{"ua":"Lenovo-A288t_TD\/S100 Linux\/2.6.35 Android\/2.3.5 Release\/02.29.2012 Browser\/AppleWebkit533.1 Mobile Safari\/533.1 FlyFlow\/1.4","browser":{"family":"Baidu Explorer","major":"1","minor":"4","patch":""},"os":{"family":"Android","major":"2","minor":"3","patch":"5","patch_minor":""},"device":{"family":"Lenovo A288t_TD","brand":"Lenovo","model":"A288t_TD"}}
```


## Future / Enhancements

Next step: add a `uap-clj-lambda.core.MultipleUseragentLookup` batch lookup handler.

Pull requests will be very happily considered.

__Maintained by Russell Whitaker__

## License

The MIT License (MIT)

Copyright (c) 2015-2016 Russell Whitaker

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
