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
import mapping.reads.IndividualBeneficiary
import models.UserAnswers
import models.core.pages.Address

class IndividualBeneficiaryMapper @Inject()(nameMapper: NameMapper,
                                            addressMapper: AddressMapper) extends Mapping[List[IndividualDetailsType]] {
  override def build(userAnswers: UserAnswers): Option[List[IndividualDetailsType]] = {
    val individualBeneficiaries : List[mapping.reads.IndividualBeneficiary] =
      userAnswers.get(mapping.reads.IndividualBeneficiaries).getOrElse(List.empty)

    individualBeneficiaries match {
      case Nil => None
      case list =>
        Some(
          list.map { indBen =>
            IndividualDetailsType(
              name = nameMapper.build(indBen.name),
              dateOfBirth = indBen.dateOfBirth,
              vulnerableBeneficiary = indBen.vulnerableYesNo,
              beneficiaryType = indBen.roleInCompany.map(_.toString),
              beneficiaryDiscretion = indBen.incomeYesNo,
              beneficiaryShareOfIncome = indBen.income,
              identification = identificationMap(indBen)
            )
          }
        )
    }
  }

  private def identificationMap(indBen: IndividualBeneficiary): Option[IdentificationType] = {
    val nino: Option[String] = indBen.nationalInsuranceNumber
    val address: Option[Address] = indBen.address
     (nino, address) match {
       case (None, None) => None
       case (_,_) =>
         Some(IdentificationType(
           nino = indBen.nationalInsuranceNumber,
           None,
           addressMapper.build(indBen.address))
       )
     }

  }
}
