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

package navigation

import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.{routes => rts}
import models.ReadableUserAnswers
import pages.Page
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated._
import play.api.mvc.Call

class EmploymentRelatedBeneficiaryNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String, userAnswers: ReadableUserAnswers): Call = routes(draftId)(page)(userAnswers)

  private def simpleNavigation(draftId: String): PartialFunction[Page, Call] = {
    case LargeBeneficiaryNamePage(index) => rts.AddressYesNoController.onPageLoad(index, draftId)
    case LargeBeneficiaryAddressPage(index) => rts.DescriptionController.onPageLoad(index, draftId)
    case LargeBeneficiaryAddressInternationalPage(index) => rts.DescriptionController.onPageLoad(index, draftId)
    case LargeBeneficiaryDescriptionPage(index) => rts.NumberOfBeneficiariesController.onPageLoad(index, draftId)
    case LargeBeneficiaryNumberOfBeneficiariesPage(index) => rts.CheckDetailsController.onPageLoad(index, draftId)
  }

  private def yesNoNavigation(draftId: String) : PartialFunction[Page, ReadableUserAnswers => Call] = {
    case LargeBeneficiaryAddressYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        LargeBeneficiaryAddressYesNoPage(index),
        rts.AddressUkYesNoController.onPageLoad(index, draftId),
        rts.DescriptionController.onPageLoad(index, draftId))
    case LargeBeneficiaryAddressUKYesNoPage(index) => ua =>
      yesNoNav(
        ua,
        LargeBeneficiaryAddressUKYesNoPage(index),
        rts.UkAddressController.onPageLoad(index, draftId),
        rts.NonUkAddressController.onPageLoad(index, draftId))
  }

  private def routes(draftId: String): PartialFunction[Page, ReadableUserAnswers => Call] = {
    simpleNavigation(draftId) andThen (c => (_:ReadableUserAnswers) => c) orElse
      yesNoNavigation(draftId)
  }

}
