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

package utils

import cats.data.EitherT
import cats.implicits.catsSyntaxEitherId
import errors.TrustErrors

import scala.concurrent.{ExecutionContext, Future}

object TrustEnvelope {
  type TrustEnvelope[T] = EitherT[Future, TrustErrors, T]

  //DDCE-3618 - Examples from other HMRC repos - these methods might not be needed

  def apply[T](arg: T)(implicit ec: ExecutionContext): TrustEnvelope[T] =
    EitherT.pure[Future, TrustErrors](arg)

  def apply[T](value: T): TrustEnvelope[T] =
    EitherT[Future, TrustErrors, T](Future.successful(value.asRight[TrustErrors]))

}

