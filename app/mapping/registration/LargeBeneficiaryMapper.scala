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

package mapping.registration

import mapping.reads.LargeBeneficiary
import models.LargeType
import models.registration.pages.HowManyBeneficiaries
import play.api.libs.json.JsPath
import sections.beneficiaries.LargeBeneficiaries

class LargeBeneficiaryMapper extends Mapper[LargeType, LargeBeneficiary] {

  override def jsPath: JsPath = LargeBeneficiaries.path

  override def beneficiaryType(beneficiary: LargeBeneficiary): LargeType = LargeType(
    organisationName = beneficiary.name,
    description = beneficiary.description.description,
    description1 = beneficiary.description.description1,
    description2 = beneficiary.description.description2,
    description3 = beneficiary.description.description3,
    description4 = beneficiary.description.description4,
    numberOfBeneficiary = convertNumberOfBeneficiaries(beneficiary.numberOfBeneficiaries),
    identification = beneficiary.identification,
    beneficiaryDiscretion = beneficiary.discretionYesNo,
    beneficiaryShareOfIncome = beneficiary.shareOfIncome,
    countryOfResidence = beneficiary.countryOfResidence
  )

  private def convertNumberOfBeneficiaries(numberOfBeneficiaries: HowManyBeneficiaries): String =
    numberOfBeneficiaries match {
      case HowManyBeneficiaries.Over1    => "1"
      case HowManyBeneficiaries.Over101  => "101"
      case HowManyBeneficiaries.Over201  => "201"
      case HowManyBeneficiaries.Over501  => "501"
      case HowManyBeneficiaries.Over1001 => "1001"
    }

}
