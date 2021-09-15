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
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.{routes => rts}
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated._

class EmploymentRelatedBeneficiaryNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator = new EmploymentRelatedBeneficiaryNavigator
  val index = 0

  "Employment related beneficiary navigator" must {


      "a taxable trust" must {
        val baseAnswers = emptyUserAnswers.copy(isTaxable = true)

        "Name page -> CountryOfResidence Yes No page" in {
          navigator.nextPage(LargeBeneficiaryNamePage(index), draftId, baseAnswers)
            .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> No -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
        }


        "CountryOfResidence Uk yes no page -> Yes -> Address yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence Uk yes no page -> No -> CountryOfResidence page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(rts.CountryOfResidenceController.onPageLoad(index, draftId))
        }

        "CountryOfResidence page -> Address yes no page" in {
          navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
            .mustBe(rts.AddressYesNoController.onPageLoad(index, draftId))
        }
      }

      "a non taxable trust" must {
        val baseAnswers = emptyUserAnswers.copy(isTaxable = false)

        "Name page -> CountryOfResidence Yes No page" in {
          navigator.nextPage(LargeBeneficiaryNamePage(index), draftId, baseAnswers)
            .mustBe(rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId))
        }

        "CountryOfResidence yes no page -> No -> Description page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(rts.DescriptionController.onPageLoad(index, fakeDraftId))
        }

        "CountryOfResidence yes no page -> Yes -> CountryOfResidence Uk yes no page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId, answers)
            .mustBe(rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId))
        }


        "CountryOfResidence Uk yes no page -> Yes -> Description page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(rts.DescriptionController.onPageLoad(index, fakeDraftId))
        }

        "CountryOfResidence Uk yes no page -> No -> CountryOfResidence page" in {
          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), draftId, answers)
            .mustBe(rts.CountryOfResidenceController.onPageLoad(index, draftId))
        }

        "CountryOfResidence page -> Description page" in {
          navigator.nextPage(CountryOfResidencePage(index), draftId, baseAnswers)
            .mustBe(rts.DescriptionController.onPageLoad(index, fakeDraftId))
        }
      }
    }
}
