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

package viewmodels.addAnother

import models.Status
import models.core.pages.FullName
import play.api.libs.json.{Reads, _}

case class IndividualBeneficiaryViewModel(label: Option[String],
                                          status: Status) extends ViewModel

object IndividualBeneficiaryViewModel {

  import play.api.libs.functional.syntax._

  implicit val reads: Reads[IndividualBeneficiaryViewModel] = (
    (__ \ "name").readNullable[FullName].map(_.map(_.toString)) and
      (__ \ "status").readWithDefault[Status](Status.InProgress)
    )(IndividualBeneficiaryViewModel.apply _)
}
