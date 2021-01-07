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

package controllers.register.beneficiaries.charityortrust.trust

import config.annotations.TrustBeneficiary
import controllers.actions.StandardActionSets
import controllers.actions.register.trust.NameRequiredAction
import forms.IncomePercentageFormProvider

import javax.inject.Inject
import navigation.Navigator
import pages.register.beneficiaries.charityortrust.trust.ShareOfIncomePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.FeatureFlagService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.beneficiaries.charityortrust.trust.ShareOfIncomeView

import scala.concurrent.{ExecutionContext, Future}

class ShareOfIncomeController @Inject()(
                                         override val messagesApi: MessagesApi,
                                         registrationsRepository: RegistrationsRepository,
                                         override val controllerComponents: MessagesControllerComponents,
                                         @TrustBeneficiary navigator: Navigator,
                                         standardActionSets: StandardActionSets,
                                         featureFlagService: FeatureFlagService,
                                         nameAction: NameRequiredAction,
                                         formProvider: IncomePercentageFormProvider,
                                         view: ShareOfIncomeView
                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Int] = formProvider.withPrefix("trustBeneficiaryShareOfIncome")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)) {
    implicit request =>

      val preparedForm = request.userAnswers.get(ShareOfIncomePage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm,  draftId, request.beneficiaryName, index))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).andThen(nameAction(index)).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(view(formWithErrors,  draftId, request.beneficiaryName, index)))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(ShareOfIncomePage(index), value))
            is5mld <- featureFlagService.is5mldEnabled()
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(ShareOfIncomePage(index),  draftId, is5mld, updatedAnswers))
      )
  }
}
