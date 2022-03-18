/*
 * Copyright 2022 HM Revenue & Customs
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

package models.registration.pages

import models.{Enumerable, WithName}

sealed trait WhatTypeOfBeneficiary

object WhatTypeOfBeneficiary extends Enumerable.Implicits {

  val prefix = "whatTypeOfBeneficiary"

  case object Individual extends WithName("individual") with WhatTypeOfBeneficiary
  case object ClassOfBeneficiary extends WithName("classOfBeneficiary") with WhatTypeOfBeneficiary
  case object CharityOrTrust extends WithName("charityOrTrust") with WhatTypeOfBeneficiary
  case object CompanyOrEmployment extends WithName("companyOrEmployment") with WhatTypeOfBeneficiary
  case object Other extends WithName("other") with WhatTypeOfBeneficiary

  case object Charity extends WithName("charity") with WhatTypeOfBeneficiary
  case object Trust extends WithName("trust") with WhatTypeOfBeneficiary
  case object Company extends WithName("company") with WhatTypeOfBeneficiary
  case object Employment extends WithName("employment") with WhatTypeOfBeneficiary

  val values: List[WhatTypeOfBeneficiary] = List(
    Individual, ClassOfBeneficiary, CharityOrTrust, CompanyOrEmployment, Other,
    Charity, Trust, Company, Employment
  )

  implicit val enumerable: Enumerable[WhatTypeOfBeneficiary] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
