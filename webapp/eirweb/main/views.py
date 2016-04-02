from PIL import Image
from django.http import HttpResponse

from main.models import Instruction


def get_latest_instruction(request):
    """
    Get the latest instruction
    :return:
    """
    i = Instruction().latest_instruction()
    path = i.render_instruction()

    try:
        with open(path, 'rb') as f:
            return HttpResponse(f.read(), content_type="image/png")
    except IOError:
        red = Image.new('RGBA', (1, 1), (255, 0, 0, 0))
        response = HttpResponse(content_type="image/jpeg")
        red.save(response, "JPEG")
        return response


def create_new_instruction(request):
    pass
