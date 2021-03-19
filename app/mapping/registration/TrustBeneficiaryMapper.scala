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

import mapping.reads.{TrustBeneficiaries, TrustBeneficiary}
import models.{BeneficiaryTrustType, IdentificationOrgType}
import pages.QuestionPage

import javax.inject.Inject

class TrustBeneficiaryMapper @Inject()(addressMapper: AddressMapper) extends Mapper[BeneficiaryTrustType, TrustBeneficiary] {

  override def section: QuestionPage[List[TrustBeneficiary]] = TrustBeneficiaries

  override def beneficiaryType(beneficiary: TrustBeneficiary): BeneficiaryTrustType = BeneficiaryTrustType(
    organisationName = beneficiary.name,
    beneficiaryDiscretion = beneficiary.discretionYesNo,
    beneficiaryShareOfIncome = beneficiary.shareOfIncome.map(_.toString),
    identification = identificationMap(beneficiary),
    countryOfResidence = beneficiary.countryOfResidence
  )

  private def identificationMap(beneficiary: TrustBeneficiary): Option[IdentificationOrgType] = {
    (beneficiary.ukAddress, beneficiary.internationalAddress) match {
      case (None, None) => None
      case (Some(address), _) => Some(IdentificationOrgType(None, addressMapper.build(address)))
      case (_, Some(address)) => Some(IdentificationOrgType(None, addressMapper.build(address)))
    }
  }

}
