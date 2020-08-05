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

package mapping.registration

import javax.inject.Inject
import mapping.Mapping
import mapping.reads.{LargeBeneficiaries, LargeBeneficiary}
import models.UserAnswers

class LargeBeneficiaryMapper @Inject()(nameMapper: NameMapper,
                                       addressMapper: AddressMapper) extends Mapping[List[LargeType]] {
  override def build(userAnswers: UserAnswers): Option[List[LargeType]] = {

    val beneficiaries: List[LargeBeneficiary] =
      userAnswers.get(LargeBeneficiaries).getOrElse(List.empty)

    beneficiaries match {
      case Nil => None
      case list =>
        Some(
          list.map { beneficiary =>
            LargeType(
              organisationName = beneficiary.name,
              description = beneficiary.description,
              description1 = beneficiary.description1 map(_.toString),
              description2 = beneficiary.description2 map(_.toString),
              description3 = beneficiary.description3 map(_.toString),
              description4 = beneficiary.description4 map(_.toString),
              numberOfBeneficiary = beneficiary.numberOfBeneficiary,
              identification = buildIdentification(beneficiary),
              beneficiaryDiscretion = Some(beneficiary.beneficiaryDiscretion),
              beneficiaryShareOfIncome = beneficiary.beneficiaryShareOfIncome map(_.toString)
            )
          }
        )
    }
  }

  private def buildIdentification(beneficiary: LargeBeneficiary): Option[IdentificationOrgType] = {
    (beneficiary.ukAddress, beneficiary.internationalAddress) match {
      case (None, None) => None
      case (Some(address), _) => Some(IdentificationOrgType(None, addressMapper.build(address)))
      case (_, Some(address)) => Some(IdentificationOrgType(None, addressMapper.build(address)))
    }
  }
}
