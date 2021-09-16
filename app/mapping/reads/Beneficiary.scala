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

import mapping.registration.IdentificationMapper.buildAddress
import models.core.pages.{InternationalAddress, UKAddress}
import models.{AddressType, IdentificationOrgType}

trait Beneficiary {
  def buildValue[A, B](o1: Option[A], o2: Option[A])
                      (build: A => Option[B]): Option[B] = (o1, o2) match {
    case (Some(v), _) => build(v)
    case (_, Some(v)) => build(v)
    case _ => None
  }
}

trait BeneficiaryWithAddress extends Beneficiary {
  def ukAddress: Option[UKAddress]
  def internationalAddress: Option[InternationalAddress]

  def ukOrInternationalAddress: Option[AddressType] =
    buildValue(ukAddress, internationalAddress)(buildAddress)
}

trait OrgBeneficiaryWithAddress extends BeneficiaryWithAddress {
  def identification: Option[IdentificationOrgType] = ukOrInternationalAddress match {
    case address @ Some(_) => Some(IdentificationOrgType(utr = None, address = address))
    case _ => None
  }
}
