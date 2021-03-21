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

package mapping.registration

import mapping.reads.IndividualBeneficiary
import models.IndividualDetailsType
import play.api.libs.json.JsPath
import sections.beneficiaries.IndividualBeneficiaries

class IndividualBeneficiaryMapper extends Mapper[IndividualDetailsType, IndividualBeneficiary] {

  override def jsPath: JsPath = IndividualBeneficiaries.path

  override def beneficiaryType(beneficiary: IndividualBeneficiary): IndividualDetailsType = IndividualDetailsType(
    name = beneficiary.name,
    dateOfBirth = beneficiary.dateOfBirth,
    vulnerableBeneficiary = beneficiary.vulnerableYesNo,
    beneficiaryType = beneficiary.roleInCompany.map(_.toString),
    beneficiaryDiscretion = beneficiary.incomeYesNo,
    beneficiaryShareOfIncome = beneficiary.income.map(_.toString),
    identification = beneficiary.identification,
    countryOfResidence = beneficiary.countryOfResidence,
    nationality = beneficiary.countryOfNationality,
    legallyIncapable = beneficiary.mentalCapacityYesNo.map(!_)
  )

}
