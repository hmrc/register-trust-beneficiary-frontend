/*
 * Copyright 2023 HM Revenue & Customs
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
import controllers.register.beneficiaries.other.routes._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.other._
import pages.register.beneficiaries.other.mld5.{CountryOfResidencePage, CountryOfResidenceYesNoPage, UKResidentYesNoPage}

class OtherBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks {

  private val index = 0
  private val navigator = new OtherBeneficiaryNavigator

  "Other beneficiary navigator" when {

    "a taxable trust" must {

      val baseAnswers = emptyUserAnswers.copy(isTaxable = true)

      "Description page -> Discretion yes no page" in {
        navigator.nextPage(DescriptionPage(index), draftId, baseAnswers)
          .mustBe(DiscretionYesNoController.onPageLoad(index, draftId))
      }

      "DiscretionYesNoPage -> Yes -> CountryOfResidenceYesNo Page" in {
        val answers = baseAnswers
          .set(IncomeDiscretionYesNoPage(index), true).right.get

        navigator.nextPage(IncomeDiscretionYesNoPage(index), draftId, answers)
          .mustBe(controllers.register.beneficiaries.other.mld5.routes.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
      }

      "Discretion yes no page -> No -> Share of income page" in {
        val answers = baseAnswers
          .set(IncomeDiscretionYesNoPage(index), false).right.get

        navigator.nextPage(IncomeDiscretionYesNoPage(index), draftId, answers)
          .mustBe(ShareOfIncomeController.onPageLoad(index, draftId))
      }

      "ShareOfIncomePage -> CountryOfResidenceYesNo Page" in {
        navigator.nextPage(ShareOfIncomePage(index), draftId, baseAnswers)
          .mustBe(controllers.register.beneficiaries.other.mld5.routes.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidenceYesNo -> No -> AddressYesNo Page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), false).right.get

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(controllers.register.beneficiaries.other.routes.AddressYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidenceYesNo -> Yes -> BeneficiariesAddressInUKYesNo Page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), true).right.get

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(controllers.register.beneficiaries.other.mld5.routes.UKResidentYesNoController.onPageLoad(index, draftId))
      }

      "UKResidentYesNo -> No -> CountryOfResidence Page" in {
        val answers = baseAnswers
          .set(UKResidentYesNoPage(index), false).right.get

        navigator.nextPage(UKResidentYesNoPage(index), draftId, answers)
          .mustBe(controllers.register.beneficiaries.other.mld5.routes.CountryOfResidenceController.onPageLoad(index, draftId))
      }

      "UKResidentYesNoYesNo -> Yes -> AddressYesNo Page" in {
        val answers = baseAnswers
          .set(UKResidentYesNoPage(index), true).right.get

        navigator.nextPage(UKResidentYesNoPage(index), draftId, answers)
          .mustBe(controllers.register.beneficiaries.other.routes.AddressYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidence -> AddressYesNo Page" in {
        navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
          .mustBe(controllers.register.beneficiaries.other.routes.AddressYesNoController.onPageLoad(index, draftId))
      }

      "Address yes no page -> No -> Check your answers page" in {
        val answers = baseAnswers
          .set(AddressYesNoPage(index), false).right.get

        navigator.nextPage(AddressYesNoPage(index), draftId, answers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

      "Address yes no page -> Yes -> Address in the UK yes no page" in {
        val answers = baseAnswers
          .set(AddressYesNoPage(index), true).right.get

        navigator.nextPage(AddressYesNoPage(index), draftId, answers)
          .mustBe(AddressUkYesNoController.onPageLoad(index, draftId))
      }

      "Address in the UK yes no page -> Yes -> UK address page" in {
        val answers = baseAnswers
          .set(AddressUKYesNoPage(index), true).right.get

        navigator.nextPage(AddressUKYesNoPage(index), draftId, answers)
          .mustBe(UkAddressController.onPageLoad(index, draftId))
      }

      "Address in the UK yes no page -> No -> Non-UK address page" in {
        val answers = baseAnswers
          .set(AddressUKYesNoPage(index), false).right.get

        navigator.nextPage(AddressUKYesNoPage(index), draftId, answers)
          .mustBe(NonUkAddressController.onPageLoad(index, draftId))
      }

      "UK address page -> Check your answers page" in {
        navigator.nextPage(AddressUKPage(index), draftId, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

      "Non-UK address page -> Check your answers page" in {
        navigator.nextPage(AddressInternationalPage(index), draftId, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

    }

    "a non taxable trust" must {

      val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

      "Description page -> CountryOfResidenceYesNo Page" in {
        navigator.nextPage(DescriptionPage(index), draftId, baseAnswers)
          .mustBe(controllers.register.beneficiaries.other.mld5.routes.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
      }

      "CountryOfResidenceYesNo -> No -> Check Details Page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), false).right.get

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

      "CountryOfResidenceYesNo -> Yes -> UKResidentYesNo Page" in {
        val answers = baseAnswers
          .set(CountryOfResidenceYesNoPage(index), true).right.get

        navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
          .mustBe(controllers.register.beneficiaries.other.mld5.routes.UKResidentYesNoController.onPageLoad(index, draftId))
      }

      "UKResidentYesNo -> No -> CountryOfResidence Page" in {
        val answers = baseAnswers
          .set(UKResidentYesNoPage(index), false).right.get

        navigator.nextPage(UKResidentYesNoPage(index), draftId, answers)
          .mustBe(controllers.register.beneficiaries.other.mld5.routes.CountryOfResidenceController.onPageLoad(index, draftId))
      }

      "UKResidentYesNoYesNo -> Yes -> Check Details Page" in {
        val answers = baseAnswers
          .set(UKResidentYesNoPage(index), true).right.get

        navigator.nextPage(UKResidentYesNoPage(index), draftId, answers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }

      "CountryOfResidence -> Check Details Page" in {
        navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
          .mustBe(CheckDetailsController.onPageLoad(index, draftId))
      }
    }
  }
}
