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

package utils.print

import com.google.inject.Inject
import controllers.register.beneficiaries.individualBeneficiary.mld5.{routes => ntRts}
import controllers.register.beneficiaries.individualBeneficiary.{routes => rts}
import models.UserAnswers
import pages.register.beneficiaries.individual._
import pages.register.beneficiaries.individual.mld5._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class IndividualBeneficiaryPrintHelper @Inject()(answerRowConverter: AnswerRowConverter) extends PrintHelper {

  override val beneficiaryType: String = "individualBeneficiary"

  override def answers(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                     (implicit messages: Messages): Seq[AnswerRow] = {
    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)

    Seq(
      bound.nameQuestion(NamePage(index), "individualBeneficiaryName", rts.NameController.onPageLoad(index, draftId).url),
      bound.roleInCompanyQuestion(RoleInCompanyPage(index), "individualBeneficiaryRoleInCompany", rts.RoleInCompanyController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(DateOfBirthYesNoPage(index), "individualBeneficiaryDateOfBirthYesNo", rts.DateOfBirthYesNoController.onPageLoad(index, draftId).url),
      bound.dateQuestion(DateOfBirthPage(index), "individualBeneficiaryDateOfBirth", rts.DateOfBirthController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(IncomeYesNoPage(index), "individualBeneficiaryIncomeYesNo", rts.IncomeYesNoController.onPageLoad(index, draftId).url),
      bound.percentageQuestion(IncomePage(index), "individualBeneficiaryIncome", rts.IncomeController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfNationalityYesNoPage(index), "individualBeneficiary.5mld.countryOfNationalityYesNo", ntRts.CountryOfNationalityYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfNationalityInTheUkYesNoPage(index), "individualBeneficiary.5mld.countryOfNationalityInTheUkYesNo", ntRts.CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(CountryOfNationalityInTheUkYesNoPage(index), CountryOfNationalityPage(index), "individualBeneficiary.5mld.countryOfNationality", ntRts.CountryOfNationalityController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(NationalInsuranceYesNoPage(index), "individualBeneficiaryNationalInsuranceYesNo", rts.NationalInsuranceYesNoController.onPageLoad(index, draftId).url),
      bound.ninoQuestion(NationalInsuranceNumberPage(index), "individualBeneficiaryNationalInsuranceNumber", rts.NationalInsuranceNumberController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceYesNoPage(index), "individualBeneficiary.5mld.countryOfResidenceYesNo", ntRts.CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage(index), "individualBeneficiary.5mld.countryOfResidenceInTheUkYesNo", ntRts.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(CountryOfResidenceInTheUkYesNoPage(index), CountryOfResidencePage(index), "individualBeneficiary.5mld.countryOfResidence", ntRts.CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressYesNoPage(index), "individualBeneficiaryAddressYesNo", rts.AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUKYesNoPage(index), "individualBeneficiaryAddressUKYesNo", rts.AddressUKYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(AddressUKPage(index), "individualBeneficiaryAddressUK", rts.AddressUKController.onPageLoad(index, draftId).url),
      bound.addressQuestion(AddressInternationalPage(index), "individualBeneficiaryAddressInternational", rts.AddressInternationalController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(PassportDetailsYesNoPage(index), "individualBeneficiaryPassportDetailsYesNo", rts.PassportDetailsYesNoController.onPageLoad(index, draftId).url),
      bound.passportDetailsQuestion(PassportDetailsPage(index), "individualBeneficiaryPassportDetails", rts.PassportDetailsController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(IDCardDetailsYesNoPage(index), "individualBeneficiaryIDCardDetailsYesNo", rts.IDCardDetailsYesNoController.onPageLoad(index, draftId).url),
      bound.passportDetailsQuestion(IDCardDetailsPage(index), "individualBeneficiaryIDCardDetails", rts.IDCardDetailsController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(MentalCapacityYesNoPage(index), "individualBeneficiary.5mld.mentalCapacityYesNo", ntRts.MentalCapacityYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(VulnerableYesNoPage(index), "individualBeneficiaryVulnerableYesNo", rts.VulnerableYesNoController.onPageLoad(index, draftId).url)
    ).flatten

  }
}
