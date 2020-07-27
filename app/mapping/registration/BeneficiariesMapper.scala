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
import models.UserAnswers
import play.api.Logger

class BeneficiariesMapper @Inject()(
                                     individualBeneficiaryMapper: IndividualBeneficiaryMapper,
                                     unidentifiedBeneficiaryMapper: ClassOfBeneficiariesMapper,
                                     charityBeneficiaryMapper: CharityBeneficiaryMapper,
                                     trustBeneficiaryMapper: TrustBeneficiaryMapper,
                                     companyBeneficiaryMapper: CompanyBeneficiaryMapper
                                   ) extends Mapping[BeneficiaryType] {

  override def build(userAnswers: UserAnswers): Option[BeneficiaryType] = {

    val individuals = individualBeneficiaryMapper.build(userAnswers)
    val unidentified = unidentifiedBeneficiaryMapper.build(userAnswers)
    val charity = charityBeneficiaryMapper.build(userAnswers)
    val trust = trustBeneficiaryMapper.build(userAnswers)
    val company = companyBeneficiaryMapper.build(userAnswers)

    val all = Seq(individuals, unidentified, charity, trust, company).flatten.flatten

    if (all.nonEmpty) {
      Some(
        BeneficiaryType(
          individualDetails = individuals,
          company = company,
          trust = trust,
          charity = charity,
          unidentified = unidentified,
          large = None,
          other = None
        )
      )
    } else {
      Logger.info(s"[BeneficiariesMapper][build] no beneficiaries to map")
      None
    }
  }
}
