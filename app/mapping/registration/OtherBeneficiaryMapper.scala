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
import mapping.reads.{OtherBeneficiaries, OtherBeneficiary}
import models.UserAnswers

class OtherBeneficiaryMapper @Inject()(addressMapper: AddressMapper) extends Mapping[List[OtherType]] {
  override def build(userAnswers: UserAnswers): Option[List[OtherType]] = {

    val beneficiaries: List[OtherBeneficiary] =
      userAnswers.get(OtherBeneficiaries).getOrElse(List.empty)

    beneficiaries match {
      case Nil => None
      case list =>
        Some(
          list.map { beneficiary =>
            OtherType(
              description = beneficiary.description,
              beneficiaryDiscretion = Some(beneficiary.incomeDiscretionYesNo),
              beneficiaryShareOfIncome = beneficiary.shareOfIncome map(_.toString),
              address = buildAddress(beneficiary)
            )
          }
        )
    }
  }

  private def buildAddress(beneficiary: OtherBeneficiary): Option[AddressType] = {
    (beneficiary.ukAddress, beneficiary.internationalAddress) match {
      case (None, None) => None
      case (Some(address), _) => addressMapper.build(address)
      case (_, Some(address)) => addressMapper.build(address)
    }
  }
}
