
# Register Trust Beneficiary Frontend

This service is responsible for collecting details about the beneficiaries of a trust when registering a trust.

---

### Running the service locally

To run locally using the micro-service provided by the service manager:

```
sm2 --start TRUSTS_ALL
```

or

```
sm2 --start REGISTER_TRUST_ALL
```

If you want to run your local copy, then stop the frontend ran by the service manager and run your local code by using the following (port number is 8837 but is defaulted to that in build.sbt):

```
sbt run
```

Use the following command to run your local copy with the test-only routes:

```
sbt run -Dapplication.router=testOnlyDoNotUseInAppConf.Routes
```

---

### Testing the service

This service uses [sbt-scoverage](https://github.com/scoverage/sbt-scoverage) to
provide test coverage reports.

Use the following commands to run the tests with coverage and generate a report.

Run this script before raising a PR to ensure your code changes pass the Jenkins pipeline. This runs all the unit tests with scalastyle and checks for dependency updates:

```
./run_all_tests.sh
```

### UI Tests
Start up service in SM2 as shown above then:

```
./run_suite.sh RegisterBeneficiariesRunner
```
from trusts-acceptance-tests repository.

---

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
