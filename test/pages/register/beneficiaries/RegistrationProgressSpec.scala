/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pages.register.beneficiaries

import base.SpecBase
import models.core.pages.FullName
import models.registration.pages._
import models.{Status, UserAnswers}
import pages.entitystatus._
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.individual.NamePage
import play.api.libs.json.{JsObject, Json}
import utils.RegistrationProgress

class RegistrationProgressSpec extends SpecBase {

  "Beneficiary section" must {

    "render no tag" when {

        "there is no beneficiaries in user answers" in {
          val registrationProgress = injector.instanceOf[RegistrationProgress]

          val userAnswers = emptyUserAnswers

          registrationProgress.beneficiariesStatus(userAnswers) mustBe None
        }

        "individual beneficiaries list is empty" in {
          val registrationProgress = injector.instanceOf[RegistrationProgress]

          val json = Json.parse(
            """
              |{
              |"beneficiaries" : {
              |            "whatTypeOfBeneficiary" : "Individual",
              |            "individualBeneficiaries" : [
              |            ]
              |        }
              |}
              |""".stripMargin)

          val userAnswers = UserAnswers(draftId = fakeDraftId, data = json.as[JsObject], internalAuthId = "id")

          registrationProgress.beneficiariesStatus(userAnswers) mustBe None
        }

        "class of beneficiaries list is empty" in {
          val registrationProgress = injector.instanceOf[RegistrationProgress]

          val json = Json.parse(
            """
              |{
              |"beneficiaries" : {
              |            "whatTypeOfBeneficiary" : "Individual",
              |            "classOfBeneficiaries" : [
              |            ]
              |        }
              |}
              |""".stripMargin)

          val userAnswers = UserAnswers(draftId = fakeDraftId, data = json.as[JsObject], internalAuthId = "id")

          registrationProgress.beneficiariesStatus(userAnswers) mustBe None
        }

      }

    "render in-progress tag" when {

      "there are individual beneficiaries only that are in progress" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(NamePage(0), FullName("First", None, "Last")).success.value

        registrationProgress.beneficiariesStatus(userAnswers).value mustBe Status.InProgress
      }

      "there are beneficiaries that are incomplete" in {

        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(ClassBeneficiaryDescriptionPage(0), "Description").success.value
          .set(ClassBeneficiaryStatus(0), Status.Completed).success.value
          .set(NamePage(0), FullName("First", None, "Last")).success.value

        registrationProgress.beneficiariesStatus(userAnswers).value mustBe Status.InProgress
      }

      "there are beneficiaries that are all complete, but user answered AddMore" in {

        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(ClassBeneficiaryDescriptionPage(0), "Description").success.value
          .set(ClassBeneficiaryStatus(0), Status.Completed).success.value
          .set(NamePage(0), FullName("First", None, "Last")).success.value
          .set(IndividualBeneficiaryStatus(0), Status.Completed).success.value
          .set(AddABeneficiaryPage, AddABeneficiary.YesLater).success.value

        registrationProgress.beneficiariesStatus(userAnswers).value mustBe Status.InProgress
      }

    }

    "render complete tag" when {

      "there are individual beneficiaries only that are complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(NamePage(0), FullName("First", None, "Last")).success.value
          .set(IndividualBeneficiaryStatus(0), Status.Completed).success.value
          .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

        registrationProgress.beneficiariesStatus(userAnswers).value mustBe Status.Completed
      }

      "there are beneficiaries marked as complete" in {
        val registrationProgress = injector.instanceOf[RegistrationProgress]

        val userAnswers = emptyUserAnswers
          .set(ClassBeneficiaryDescriptionPage(0), "Description").success.value
          .set(ClassBeneficiaryStatus(0), Status.Completed).success.value
          .set(NamePage(0), FullName("First", None, "Last")).success.value
          .set(IndividualBeneficiaryStatus(0), Status.Completed).success.value
          .set(AddABeneficiaryPage, AddABeneficiary.NoComplete).success.value

        registrationProgress.beneficiariesStatus(userAnswers).value mustBe Status.Completed
      }

    }

  }
}
