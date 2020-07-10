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

package navigation

import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import models.{UserAnswers, _}
import navigation.routes.BeneficiaryRoutes
import pages._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class Navigator @Inject()(config: FrontendAppConfig) {

  private def defaultRoute(draftId: String): PartialFunction[Page, AffinityGroup => ReadableUserAnswers => Call] = {
    case _ => _ => _ => controllers.routes.IndexController.onPageLoad(draftId)
  }

  protected def route(draftId: String): PartialFunction[Page, AffinityGroup => ReadableUserAnswers => Call] =
      BeneficiaryRoutes.route(draftId, config) orElse
      defaultRoute(draftId)

  def nextPage(page: Page, mode: Mode, draftId: String, af :AffinityGroup = AffinityGroup.Organisation): ReadableUserAnswers => Call = mode match {
    case NormalMode =>
      route(draftId)(page)(af)
    case CheckMode =>
      route(draftId)(page)(af)
  }

}
