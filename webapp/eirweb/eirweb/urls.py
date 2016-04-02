from django.conf.urls import include, url
from django.contrib import admin

from main import views

urlpatterns = [
    url(r'^admin/', include(admin.site.urls)),

    url(r'^api/instruction/latest', views.get_latest_instruction),
]
