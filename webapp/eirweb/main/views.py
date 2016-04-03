from base64 import b64decode

import random
from PIL import Image as PILImage
from django.http import HttpResponse, JsonResponse
from django.template.response import TemplateResponse
from django.views.decorators.csrf import csrf_exempt

import os

from main.models import Instruction, Image


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
        red = PILImage.new('RGBA', (1, 1), (255, 0, 0, 0))
        response = HttpResponse(content_type="image/jpeg")
        red.save(response, "JPEG")
        return response

def get_latest_text(request):
    if random.randint(0, 4) == 0:
        return HttpResponse("switchVid")
    else:
        return HttpResponse("You wanted some random shit. Here get it %d times!" % random.randint(1, 100))


def get_image(request):
    id = request.GET.get('id', None)
    if id is None:
        # Return all images relevant images
        images = Image.get_relevant_images()

        # Transfer images to json
        images_data = []
        for image in images:
            im_dic = {
                'path': '/image?id=%d' % image.id
            }
            images_data.append(im_dic)
        return JsonResponse({'images': images_data})
    else:
        # Return the image
        thumbnail = request.GET.get('thumbnail', '0')
        image = Image.objects.get(pk=id)
        if thumbnail == '1':
            path = '/tmp/im.png'
            resize_and_crop(image.filename, path, (200, 200))
            with open(path, 'rb') as f:
                return HttpResponse(f.read(), content_type="png")
        else:
            with open(image.filename, 'rb') as f:
                return HttpResponse(f.read(), content_type="png")


def resize_and_crop(img_path, modified_path, size, crop_type='top'):
    """
    Resize and crop an image to fit the specified size.
    args:
        img_path: path for the image to resize.
        modified_path: path to store the modified image.
        size: `(width, height)` tuple.
        crop_type: can be 'top', 'middle' or 'bottom', depending on this
            value, the image will cropped getting the 'top/left', 'midle' or
            'bottom/rigth' of the image to fit the size.
    raises:
        Exception: if can not open the file in img_path of there is problems
            to save the image.
        ValueError: if an invalid `crop_type` is provided.
    """
    # If height is higher we resize vertically, if not we resize horizontally
    img = PILImage.open(img_path)
    # Get current and desired ratio for the images
    img_ratio = img.size[0] / float(img.size[1])
    ratio = size[0] / float(size[1])
    #The image is scaled/cropped vertically or horizontally depending on the ratio
    if ratio > img_ratio:
        img = img.resize((size[0], int(size[0] * img.size[1] / img.size[0])),
                PILImage.ANTIALIAS)
        # Crop in the top, middle or bottom
        if crop_type == 'top':
            box = (0, 0, img.size[0], size[1])
        elif crop_type == 'middle':
            box = (0, (img.size[1] - size[1]) / 2, img.size[0], (img.size[1] + size[1]) / 2)
        elif crop_type == 'bottom':
            box = (0, img.size[1] - size[1], img.size[0], img.size[1])
        else :
            raise ValueError('ERROR: invalid value for crop_type')
        img = img.crop(box)
    elif ratio < img_ratio:
        img = img.resize((int(size[1] * img.size[0] / img.size[1]), size[1]),
                PILImage.ANTIALIAS)
        # Crop in the top, middle or bottom
        if crop_type == 'top':
            box = (0, 0, size[0], img.size[1])
        elif crop_type == 'middle':
            box = ((img.size[0] - size[0]) / 2, 0, (img.size[0] + size[0]) / 2, img.size[1])
        elif crop_type == 'bottom':
            box = (img.size[0] - size[0], 0, img.size[0], img.size[1])
        else :
            raise ValueError('ERROR: invalid value for crop_type')
        img = img.crop(box)
    else :
        img = img.resize((size[0], size[1]),
                PILImage.ANTIALIAS)
        # If the scale is the same, we do not need to crop
    img.save(modified_path)

@csrf_exempt
def create_new_instruction(request):
    """
    Create a new instruction. Can either be based on an existing instruction (using "instruction=<id>")
    or based on an image (using "image=<id>"). Instruction is stronger then image.
    """

    instruction = Instruction()
    instruction.filepath = "/foo"
    instruction.save()

    new_instruction_file = request.POST.get('instruction', None)
    if new_instruction_file is None:
        return HttpResponse("You fucked up!")

    # Save instruction to disk
    data = new_instruction_file.partition('base64,')[2]
    image_data = b64decode(data)
    path = 'instructions/%d.png' % instruction.id
    with open(path, 'wb') as f:
        f.write(image_data)

    instruction.filepath = path
    instruction.save()

    return HttpResponse(status=200)


def reset(request):
    Instruction.objects.all().delete()


def update_images(request):
    """
    Rereads the images from
    :param request:
    :return:
    """
    Image.objects.all().delete()

    files = os.listdir('images')
    files = [f for f in files if f[-3:] == 'png']
    for f in files:
        image = Image()
        image.filename = 'images/%s' % f
        image.save()

    return HttpResponse("Update images (Count: %d)" % len(files))


def main_app(request):
    context = {
        'images': Image.get_relevant_images()
    }
    return TemplateResponse(request, "main/index.html", context)

