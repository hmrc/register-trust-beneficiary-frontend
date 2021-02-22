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
import controllers.register.beneficiaries.individualBeneficiary.mld5.routes._
import controllers.register.beneficiaries.individualBeneficiary.routes._
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
      bound.nameQuestion(NamePage(index), "individualBeneficiaryName", NameController.onPageLoad(index, draftId).url),
      bound.roleInCompanyQuestion(RoleInCompanyPage(index), "individualBeneficiaryRoleInCompany", RoleInCompanyController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(DateOfBirthYesNoPage(index), "individualBeneficiaryDateOfBirthYesNo", DateOfBirthYesNoController.onPageLoad(index, draftId).url),
      bound.dateQuestion(DateOfBirthPage(index), "individualBeneficiaryDateOfBirth", DateOfBirthController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(IncomeYesNoPage(index), "individualBeneficiaryIncomeYesNo", IncomeYesNoController.onPageLoad(index, draftId).url),
      bound.percentageQuestion(IncomePage(index), "individualBeneficiaryIncome", IncomeController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfNationalityYesNoPage(index), "individualBeneficiary.5mld.countryOfNationalityYesNo", CountryOfNationalityYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfNationalityInTheUkYesNoPage(index), "individualBeneficiary.5mld.countryOfNationalityInTheUkYesNo", CountryOfNationalityInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(CountryOfNationalityInTheUkYesNoPage(index), CountryOfNationalityPage(index), "individualBeneficiary.5mld.countryOfNationality", CountryOfNationalityController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(NationalInsuranceYesNoPage(index), "individualBeneficiaryNationalInsuranceYesNo", NationalInsuranceYesNoController.onPageLoad(index, draftId).url),
      bound.ninoQuestion(NationalInsuranceNumberPage(index), "individualBeneficiaryNationalInsuranceNumber", NationalInsuranceNumberController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceYesNoPage(index), "individualBeneficiary.5mld.countryOfResidenceYesNo", CountryOfResidenceYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage(index), "individualBeneficiary.5mld.countryOfResidenceInTheUkYesNo", CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url),
      bound.countryQuestion(CountryOfResidenceInTheUkYesNoPage(index), CountryOfResidencePage(index), "individualBeneficiary.5mld.countryOfResidence", CountryOfResidenceController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressYesNoPage(index), "individualBeneficiaryAddressYesNo", AddressYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(AddressUKYesNoPage(index), "individualBeneficiaryAddressUKYesNo", AddressUKYesNoController.onPageLoad(index, draftId).url),
      bound.addressQuestion(AddressUKPage(index), "individualBeneficiaryAddressUK", AddressUKController.onPageLoad(index, draftId).url),
      bound.addressQuestion(AddressInternationalPage(index), "individualBeneficiaryAddressInternational", AddressInternationalController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(PassportDetailsYesNoPage(index), "individualBeneficiaryPassportDetailsYesNo", PassportDetailsYesNoController.onPageLoad(index, draftId).url),
      bound.passportDetailsQuestion(PassportDetailsPage(index), "individualBeneficiaryPassportDetails", PassportDetailsController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(IDCardDetailsYesNoPage(index), "individualBeneficiaryIDCardDetailsYesNo", IDCardDetailsYesNoController.onPageLoad(index, draftId).url),
      bound.passportDetailsQuestion(IDCardDetailsPage(index), "individualBeneficiaryIDCardDetails", IDCardDetailsController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(MentalCapacityYesNoPage(index), "individualBeneficiary.5mld.mentalCapacityYesNo", MentalCapacityYesNoController.onPageLoad(index, draftId).url),
      bound.yesNoQuestion(VulnerableYesNoPage(index), "individualBeneficiaryVulnerableYesNo", VulnerableYesNoController.onPageLoad(index, draftId).url)
    ).flatten

  }
}
