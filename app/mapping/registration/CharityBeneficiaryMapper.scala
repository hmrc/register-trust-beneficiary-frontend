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

import javax.inject.Inject
import mapping.Mapping
import mapping.reads.CharityBeneficiary
import models.UserAnswers

class CharityBeneficiaryMapper @Inject()(addressMapper: AddressMapper) extends Mapping[List[CharityType]] {

  override def build(userAnswers: UserAnswers): Option[List[CharityType]] = {

    val charityBeneficiaries : List[CharityBeneficiary] =
      userAnswers.get(mapping.reads.CharityBeneficiaries).getOrElse(List.empty)

    charityBeneficiaries match {
      case Nil => None
      case list =>
        Some(
          list.map { charBen =>
            CharityType(
              organisationName = charBen.name,
              beneficiaryDiscretion = Some(charBen.howMuchIncome.isEmpty),
              beneficiaryShareOfIncome = charBen.howMuchIncome.map(_.toString),
              identification = identificationMap(charBen),
              countryOfResidence = charBen.countryOfResidence)
          }
        )
    }
  }

  private def identificationMap(beneficiary: CharityBeneficiary): Option[IdentificationOrgType] = {
    (beneficiary.ukAddress, beneficiary.internationalAddress) match {
      case (None, None) => None
      case (Some(address), _) => Some(IdentificationOrgType(None, addressMapper.build(address)))
      case (_, Some(address)) => Some(IdentificationOrgType(None, addressMapper.build(address)))
    }
  }
}