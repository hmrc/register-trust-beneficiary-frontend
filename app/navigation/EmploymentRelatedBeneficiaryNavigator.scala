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

import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.{routes => rts}
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5.{routes => mld5Rts}
import models.ReadableUserAnswers
import pages.Page
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated._
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.mld5._
import play.api.mvc.Call

class EmploymentRelatedBeneficiaryNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call =
    routes(draftId)(page)(userAnswers)

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] =
    simpleNavigation(draftId) orElse
      yesNoNavigation(draftId)

  private def simpleNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case LargeBeneficiaryNamePage(index)                  => _ => mld5Rts.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    case LargeBeneficiaryAddressPage(index)               => _ => rts.DescriptionController.onPageLoad(index, draftId)
    case LargeBeneficiaryAddressInternationalPage(index)  => _ => rts.DescriptionController.onPageLoad(index, draftId)
    case LargeBeneficiaryDescriptionPage(index)           => _ => rts.NumberOfBeneficiariesController.onPageLoad(index, draftId)
    case LargeBeneficiaryNumberOfBeneficiariesPage(index) => _ => rts.CheckDetailsController.onPageLoad(index, draftId)
    case CountryOfResidencePage(index)                    => ua => navigateAwayFromResidencyQuestions(draftId, index, ua.isTaxable)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    case page @ LargeBeneficiaryAddressYesNoPage(index)   =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = rts.AddressUkYesNoController.onPageLoad(index, draftId),
          noCall = rts.DescriptionController.onPageLoad(index, draftId)
        )
    case page @ LargeBeneficiaryAddressUKYesNoPage(index) =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = rts.UkAddressController.onPageLoad(index, draftId),
          noCall = rts.NonUkAddressController.onPageLoad(index, draftId)
        )
    case page @ CountryOfResidenceYesNoPage(index)        =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
          noCall = navigateAwayFromResidencyQuestions(draftId, index, ua.isTaxable)
        )
    case page @ CountryOfResidenceInTheUkYesNoPage(index) =>
      ua =>
        yesNoNav(
          ua = ua,
          fromPage = page,
          yesCall = navigateAwayFromResidencyQuestions(draftId, index, ua.isTaxable),
          noCall = mld5Rts.CountryOfResidenceController.onPageLoad(index, draftId)
        )
  }

  private def navigateAwayFromResidencyQuestions(draftId: String, index: Int, isTaxable: Boolean): Call =
    if (isTaxable) {
      rts.AddressYesNoController.onPageLoad(index, draftId)
    } else {
      rts.DescriptionController.onPageLoad(index, draftId)
    }

}
