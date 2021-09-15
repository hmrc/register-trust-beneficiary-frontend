/*
 * Copyright 2021 HM Revenue & Customs
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

package navigation

import base.SpecBase
import controllers.register.beneficiaries.individualBeneficiary.routes._
import models.registration.pages.KindOfTrust._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.KindOfTrustPage
import pages.register.beneficiaries.individual._
import utils.Constants.ES

class IndividualBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  val navigator = new IndividualBeneficiaryNavigator

  val index = 0

  "Individual beneficiary navigator" must {

      "taxable " when {
        val baseAnswers = emptyUserAnswers.copy(isTaxable = true)

        "Name page" when {

          "a trust for the employees of a company" must {
            "-> Role in the company page" in {
              val answers = emptyUserAnswers
                .set(KindOfTrustPage, Employees).success.value

              navigator.nextPage(NamePage(index), fakeDraftId, answers)
                .mustBe(RoleInCompanyController.onPageLoad(index, fakeDraftId))
            }
          }

          "not a trust for the employees of a company" must {
            "-> Do you know date of birth page" in {
              val answers = emptyUserAnswers
                .set(KindOfTrustPage, Deed).success.value

              navigator.nextPage(NamePage(index), fakeDraftId, answers)
                .mustBe(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
            }
          }
        }

        "Role in company page -> Do you know date of birth page" in {
          navigator.nextPage(RoleInCompanyPage(index), fakeDraftId, baseAnswers)
            .mustBe(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
        }

        "Do you know date of birth page" when {
          "Yes" must {
            "-> Date of birth page" in {
              val answers = baseAnswers
                .set(DateOfBirthYesNoPage(index), true).success.value

              navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
                .mustBe(DateOfBirthController.onPageLoad(index, fakeDraftId))
            }
          }

          "No" must {
            "-> Do trustees have discretion page" in {
              val answers = baseAnswers
                .set(DateOfBirthYesNoPage(index), false).success.value

              navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
                .mustBe(IncomeYesNoController.onPageLoad(index, fakeDraftId))
            }
          }
        }

        "Date of birth page -> Do trustees have discretion page" in {
          navigator.nextPage(DateOfBirthPage(index), fakeDraftId, baseAnswers)
            .mustBe(IncomeYesNoController.onPageLoad(index, fakeDraftId))
        }

        "Discretion yes no page -> Yes -> CountryOfNationality Yes No page" in {

          val answers = baseAnswers
            .set(IncomeYesNoPage(index), true).success.value

          navigator.nextPage(IncomeYesNoPage(index), draftId, answers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationality yes no page -> No -> Nino yes no page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, answers)
            .mustBe(NationalInsuranceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationality yes no page -> Yes -> CountryOfNationality Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, answers)
            .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationalityInUK yes no page -> No -> CountryOfNationality page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), draftId, answers)
            .mustBe(CountryOfNationalityController.onPageLoad(index, draftId))
        }

        "CountryOfNationalityInUK yes no page -> Yes -> Nino yes no page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), draftId, answers)
            .mustBe(NationalInsuranceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationality -> Nino Yes No page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityPage(index), ES).success.value

          navigator.nextPage(CountryOfNationalityPage(index), draftId, answers)
            .mustBe(NationalInsuranceYesNoController.onPageLoad(index, draftId))

        }

        "NationalInsuranceNumber yes no page -> No -> CountryOfResidence yes no page" in {
          val answers = baseAnswers
            .set(NationalInsuranceYesNoPage(index), false).success.value

          navigator.nextPage(NationalInsuranceYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> No -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(AddressYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidenceInUK yes no page -> No -> CountryOfResidence page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceController.onPageLoad(index, draftId))
        }

        "CountryOfResidenceInUK yes no page -> Yes -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(AddressYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence (with Nino) -> MentalCapacityYesNo page" in {

          val answers = baseAnswers
            .set(NationalInsuranceYesNoPage(index), true).success.value
            .set(CountryOfResidencePage(index), ES).success.value

          navigator.nextPage(CountryOfResidencePage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))

        }

        "CountryOfResidence (with No Nino) -> Address Yes No page" in {

          val answers = baseAnswers
            .set(NationalInsuranceYesNoPage(index), false).success.value
            .set(CountryOfResidencePage(index), ES).success.value

          navigator.nextPage(CountryOfResidencePage(index), draftId, answers)
            .mustBe(AddressYesNoController.onPageLoad(index, draftId))

        }

        "Address Yes No page -> No -> MentalCapacityYesNo page" in {

          val answers = baseAnswers
            .set(AddressYesNoPage(index), false).success.value

          navigator.nextPage(AddressYesNoPage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))

        }

        "PassportDetailsPage -> MentalCapacityYesNo page" in {

          navigator.nextPage(PassportDetailsPage(index), fakeDraftId, baseAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))

        }

        "IDCardDetails Yes No page -> No -> MentalCapacityYesNo page" in {

          val answers = baseAnswers
            .set(IDCardDetailsYesNoPage(index), false).success.value

          navigator.nextPage(IDCardDetailsYesNoPage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))

        }

        "IDCardDetails page -> MentalCapacityYesNo page" in {

          navigator.nextPage(IDCardDetailsPage(index), fakeDraftId, baseAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))

        }

        "MentalCapacityYesNo page -> Has VPE1 been submitted page" in {

          navigator.nextPage(MentalCapacityYesNoPage(index), fakeDraftId, baseAnswers)
            .mustBe(VulnerableYesNoController.onPageLoad(index, fakeDraftId))

        }

        "Has VPE1 been submitted page -> Check answers page" in {
          navigator.nextPage(VulnerableYesNoPage(index), fakeDraftId, emptyUserAnswers)
            .mustBe(AnswersController.onPageLoad(index, fakeDraftId))
        }
      }

      "none taxable " when {
        val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

        "Name page" when {

          "a trust for the employees of a company" must {
            "-> Do you know date of birth page" in {
              val answers = baseAnswers
                .set(KindOfTrustPage, Employees).success.value

              navigator.nextPage(NamePage(index), fakeDraftId, answers)
                .mustBe(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
            }
          }

          "not a trust for the employees of a company" must {
            "-> Do you know date of birth page" in {
              val answers = baseAnswers
                .set(KindOfTrustPage, Deed).success.value

              navigator.nextPage(NamePage(index), fakeDraftId, answers)
                .mustBe(DateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
            }
          }
        }

        "Do you know date of birth page" when {
          "Yes" must {
            "-> Date of birth page" in {
              val answers = baseAnswers
                .set(DateOfBirthYesNoPage(index), true).success.value

              navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
                .mustBe(DateOfBirthController.onPageLoad(index, fakeDraftId))
            }
          }

          "No" must {
            "->  Country Of Nationality page" in {
              val answers = baseAnswers
                .set(DateOfBirthYesNoPage(index), false).success.value

              navigator.nextPage(DateOfBirthYesNoPage(index), fakeDraftId, answers)
                .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
            }
          }
        }

        "Date of birth page -> Country Of Nationality page" in {
          navigator.nextPage(DateOfBirthPage(index), fakeDraftId, baseAnswers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
        }

        "CountryOfNationality yes no page -> No -> CountryOfResidenceYesNo page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationality yes no page -> Yes -> Country Of Nationality In The Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage(index), draftId, answers)
            .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationalityInUK yes no page -> No -> CountryOfNationality page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), draftId, answers)
            .mustBe(CountryOfNationalityController.onPageLoad(index, draftId))
        }

        "CountryOfNationalityInUK yes no page -> Yes -> Country Of Residence yes no page" in {
          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfNationality -> Country Of Residence yes no page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityPage(index), ES).success.value

          navigator.nextPage(CountryOfNationalityPage(index), draftId, answers)
            .mustBe(CountryOfResidenceYesNoController.onPageLoad(index, draftId))

        }

        "CountryOfResidence yes no page -> No -> MentalCapacityYesNo page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidenceInUK yes no page -> No -> CountryOfResidence page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(CountryOfResidenceController.onPageLoad(index, draftId))
        }

        "CountryOfResidenceInUK yes no page -> Yes -> MentalCapacityYesNo page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence (with Nino) -> MentalCapacityYesNo page" in {

          val answers = baseAnswers
            .set(NationalInsuranceYesNoPage(index), true).success.value
            .set(CountryOfResidencePage(index), ES).success.value

          navigator.nextPage(CountryOfResidencePage(index), draftId, answers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, draftId))

        }

        "MentalCapacityYesNo page -> Check answers page" in {

          navigator.nextPage(MentalCapacityYesNoPage(index), fakeDraftId, baseAnswers)
            .mustBe(AnswersController.onPageLoad(index, fakeDraftId))

        }
      }
    }
}
