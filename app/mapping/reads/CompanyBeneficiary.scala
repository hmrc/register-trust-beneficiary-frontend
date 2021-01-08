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

package mapping.reads

import models.core.pages.{InternationalAddress, UKAddress}
import play.api.libs.json.{Format, Json}

final case class CompanyBeneficiary(name: String,
                                    ukAddress : Option[UKAddress],
                                    countryOfResidence: Option[String],
                                    internationalAddress : Option[InternationalAddress],
                                    income: Option[Int],
                                    incomeYesNo: Boolean
                                   ) {
}

object CompanyBeneficiary {
  implicit val classFormat: Format[CompanyBeneficiary] = Json.format[CompanyBeneficiary]
}
