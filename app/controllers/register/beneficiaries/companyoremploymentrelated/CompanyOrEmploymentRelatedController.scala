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

package controllers.register.beneficiaries.companyoremploymentrelated

import controllers.actions._
import forms.CompanyOrEmploymentRelatedBeneficiaryTypeFormProvider
import javax.inject.Inject
import models.CompanyOrEmploymentRelatedToAdd
import navigation.Navigator
import pages.register.beneficiaries.companyoremploymentrelated.CompanyOrEmploymentRelatedPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.register.beneficiaries.companyoremploymentrelated.CompanyOrEmploymentRelatedView

import scala.concurrent.{ExecutionContext, Future}

class CompanyOrEmploymentRelatedController @Inject()(
                                                      override val messagesApi: MessagesApi,
                                                      standardActionSets: StandardActionSets,
                                                      navigator: Navigator,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: CompanyOrEmploymentRelatedView,
                                                      formProvider: CompanyOrEmploymentRelatedBeneficiaryTypeFormProvider,
                                                      repository: RegistrationsRepository
                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[CompanyOrEmploymentRelatedToAdd] = formProvider()

  def onPageLoad(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId) {
    implicit request => Ok(view(form, draftId))
  }

  def onSubmit(draftId: String): Action[AnyContent] = standardActionSets.identifiedUserWithData(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, draftId))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CompanyOrEmploymentRelatedPage, value))
            _ <- repository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CompanyOrEmploymentRelatedPage, draftId, updatedAnswers))
      )
  }
}
